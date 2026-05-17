from typing import List, Type

from sqlfluff.core.plugin import hookimpl
from sqlfluff.core.rules import BaseRule


@hookimpl
def get_rules() -> List[Type[BaseRule]]:
    """Get plugin rules."""

    from agentic_sqlfluff_rules.rules import (
        Rule_L901,
        Rule_L902,
        Rule_L903,
        Rule_L904,
        Rule_L905,
        Rule_L906,
        Rule_L907,
        Rule_L908,
    )

    return [
        Rule_L901,
        Rule_L902,
        Rule_L903,
        Rule_L904,
        Rule_L905,
        Rule_L906,
        Rule_L907,
        Rule_L908,
    ]
