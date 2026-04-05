from __future__ import annotations

import re
from dataclasses import dataclass
from typing import Iterable, List

from sqlfluff.core.rules import BaseRule, LintResult, RuleContext
from sqlfluff.core.rules.crawlers import SegmentSeekerCrawler

CREATE_TABLE_RE = re.compile(
    r"CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?(?P<name>[^\s(]+)",
    re.IGNORECASE,
)
PRIMARY_KEY_RE = re.compile(r"PRIMARY\s+KEY\s*\((?P<cols>[^)]+)\)", re.IGNORECASE)
FOREIGN_KEY_RE = re.compile(
    r"(?:CONSTRAINT\s+(?P<name>[^\s]+)\s+)?"
    r"FOREIGN\s+KEY\s*\((?P<cols>[^)]+)\)\s+"
    r"REFERENCES\s+(?P<ref_table>[^\s(]+)",
    re.IGNORECASE,
)
NOT_NULL_RE = re.compile(r"\bNOT\s+NULL\b", re.IGNORECASE)
PRIMARY_KEY_INLINE_RE = re.compile(r"\bPRIMARY\s+KEY\b", re.IGNORECASE)

SNAKE_CASE_RE = re.compile(r"^[a-z][a-z0-9_]*$")


@dataclass
class ColumnDefinition:
    name: str
    raw_type: str
    not_null: bool
    inline_primary_key: bool


@dataclass
class ForeignKeyConstraint:
    name: str | None
    columns: List[str]
    ref_table: str


@dataclass
class TableDefinition:
    name: str
    columns: List[ColumnDefinition]
    primary_keys: List[str]
    foreign_keys: List[ForeignKeyConstraint]


def _normalize_identifier(raw: str) -> str:
    value = raw.strip()
    if value.startswith("\"") and value.endswith("\""):
        value = value[1:-1]
    if "." in value:
        value = value.split(".")[-1]
    return value


def _extract_table_name(statement: str) -> str | None:
    match = CREATE_TABLE_RE.search(statement)
    if not match:
        return None
    return _normalize_identifier(match.group("name"))


def _extract_create_table_body(statement: str) -> str | None:
    match = CREATE_TABLE_RE.search(statement)
    if not match:
        return None
    start = statement.find("(", match.end())
    if start == -1:
        return None
    depth = 0
    for idx in range(start, len(statement)):
        char = statement[idx]
        if char == "(":
            depth += 1
        elif char == ")":
            depth -= 1
            if depth == 0:
                return statement[start + 1 : idx]
    return None


def _split_top_level(body: str) -> List[str]:
    parts: List[str] = []
    buffer: List[str] = []
    depth = 0
    for char in body:
        if char == "(":
            depth += 1
        elif char == ")":
            depth -= 1
        elif char == "," and depth == 0:
            part = "".join(buffer).strip()
            if part:
                parts.append(part)
            buffer = []
            continue
        buffer.append(char)
    tail = "".join(buffer).strip()
    if tail:
        parts.append(tail)
    return parts


def _parse_type(definition: str) -> str:
    depth = 0
    buffer: List[str] = []
    for char in definition:
        if char == "(":
            depth += 1
        elif char == ")":
            depth -= 1
        elif char.isspace() and depth == 0:
            break
        buffer.append(char)
    return "".join(buffer).strip()


def _parse_column(definition: str) -> ColumnDefinition | None:
    stripped = definition.strip()
    if not stripped:
        return None
    if stripped.upper().startswith("CONSTRAINT "):
        return None
    if stripped.upper().startswith("PRIMARY KEY"):
        return None
    if stripped.upper().startswith("FOREIGN KEY"):
        return None
    tokens = stripped.split(None, 1)
    if len(tokens) < 2:
        return None
    name = _normalize_identifier(tokens[0])
    rest = tokens[1]
    raw_type = _parse_type(rest)
    not_null = bool(NOT_NULL_RE.search(rest))
    inline_pk = bool(PRIMARY_KEY_INLINE_RE.search(rest))
    return ColumnDefinition(name=name, raw_type=raw_type, not_null=not_null, inline_primary_key=inline_pk)


def _parse_primary_keys(definitions: Iterable[str]) -> List[str]:
    keys: List[str] = []
    for definition in definitions:
        match = PRIMARY_KEY_RE.search(definition)
        if not match:
            continue
        cols = match.group("cols").split(",")
        keys.extend([_normalize_identifier(col) for col in cols])
    return keys


def _parse_foreign_keys(definitions: Iterable[str]) -> List[ForeignKeyConstraint]:
    constraints: List[ForeignKeyConstraint] = []
    for definition in definitions:
        match = FOREIGN_KEY_RE.search(definition)
        if not match:
            continue
        name = match.group("name")
        columns = [_normalize_identifier(col) for col in match.group("cols").split(",")]
        ref_table = _normalize_identifier(match.group("ref_table"))
        constraints.append(ForeignKeyConstraint(name=name, columns=columns, ref_table=ref_table))
    return constraints


def _parse_table(statement: str) -> TableDefinition | None:
    table_name = _extract_table_name(statement)
    if not table_name:
        return None
    body = _extract_create_table_body(statement)
    if body is None:
        return None
    definitions = _split_top_level(body)
    columns = [column for definition in definitions if (column := _parse_column(definition))]
    primary_keys = _parse_primary_keys(definitions)
    foreign_keys = _parse_foreign_keys(definitions)
    return TableDefinition(
        name=table_name,
        columns=columns,
        primary_keys=primary_keys,
        foreign_keys=foreign_keys,
    )


def _is_snake_case(value: str) -> bool:
    return bool(SNAKE_CASE_RE.match(value))


def _is_plural(value: str) -> bool:
    return value.endswith("s")


def _normalize_type(value: str) -> str:
    return re.sub(r"\s+", "", value.upper())


def _normalize_fk_expected(ref_table: str) -> str:
    base = _normalize_identifier(ref_table)
    if base.endswith("s"):
        base = base[:-1]
    return f"{base}_id"


class Rule_L901(BaseRule):
    name = "agentic.table_name_plural_snake_case"
    code = "L901"
    description = "Table names must be snake_case and plural."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        if _is_snake_case(definition.name) and _is_plural(definition.name):
            return None
        return LintResult(
            anchor=context.segment,
            description=f"Table name `{definition.name}` must be snake_case plural.",
        )


class Rule_L902(BaseRule):
    name = "agentic.column_name_snake_case"
    code = "L902"
    description = "Column names must be snake_case."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        for column in definition.columns:
            if not _is_snake_case(column.name):
                return LintResult(
                    anchor=context.segment,
                    description=f"Column name `{column.name}` must be snake_case.",
                )
        return None


class Rule_L903(BaseRule):
    name = "agentic.primary_key_id"
    code = "L903"
    description = "Single primary key columns must be named `id`."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        inline_pks = [column.name for column in definition.columns if column.inline_primary_key]
        primary_keys = list(dict.fromkeys(definition.primary_keys + inline_pks))
        if len(primary_keys) == 1 and primary_keys[0] != "id":
            return LintResult(
                anchor=context.segment,
                description=f"Primary key `{primary_keys[0]}` must be named `id`.",
            )
        return None


class Rule_L904(BaseRule):
    name = "agentic.foreign_key_column_naming"
    code = "L904"
    description = "Foreign key columns must be named `{ref}_id`."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        for fk in definition.foreign_keys:
            expected = _normalize_fk_expected(fk.ref_table)
            for column in fk.columns:
                if column != expected:
                    return LintResult(
                        anchor=context.segment,
                        description=(
                            "Foreign key column name `{} ` must be `{}`."
                        ).format(column, expected),
                    )
        return None


class Rule_L905(BaseRule):
    name = "agentic.foreign_key_constraint_naming"
    code = "L905"
    description = "Foreign key constraint names must be fk_{table}_{ref_table}."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        for fk in definition.foreign_keys:
            expected = f"fk_{definition.name}_{_normalize_identifier(fk.ref_table)}"
            if not fk.name or fk.name != expected:
                return LintResult(
                    anchor=context.segment,
                    description=(
                        "Foreign key constraint name must be `{}`."
                    ).format(expected),
                )
        return None


class Rule_L906(BaseRule):
    name = "agentic.id_type_varchar36"
    code = "L906"
    description = "id and *_id columns must use VARCHAR(36)."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        for column in definition.columns:
            if column.name == "id" or column.name.endswith("_id"):
                if _normalize_type(column.raw_type) != "VARCHAR(36)":
                    return LintResult(
                        anchor=context.segment,
                        description=(
                            "Column `{}` must use VARCHAR(36)."
                        ).format(column.name),
                    )
        return None


class Rule_L907(BaseRule):
    name = "agentic.columns_not_null"
    code = "L907"
    description = "Columns must be NOT NULL unless part of PRIMARY KEY."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        inline_pks = {column.name for column in definition.columns if column.inline_primary_key}
        primary_keys = set(definition.primary_keys) | inline_pks
        for column in definition.columns:
            if column.name in primary_keys:
                continue
            if not column.not_null:
                return LintResult(
                    anchor=context.segment,
                    description=f"Column `{column.name}` must be NOT NULL.",
                )
        return None


class Rule_L908(BaseRule):
    name = "agentic.timestamp_type"
    code = "L908"
    description = "*_at columns must use TIMESTAMPTZ."
    crawl_behaviour = SegmentSeekerCrawler({"create_table_statement"})

    def _eval(self, context: RuleContext):
        definition = _parse_table(context.segment.raw)
        if not definition:
            return None
        for column in definition.columns:
            if column.name.endswith("_at"):
                if _normalize_type(column.raw_type) != "TIMESTAMPTZ":
                    return LintResult(
                        anchor=context.segment,
                        description=(
                            "Column `{}` must use TIMESTAMPTZ."
                        ).format(column.name),
                    )
        return None
