import { describe, expect, it } from "vitest";

import { parseSearchParams } from "../search-criteria";

describe("parseSearchParams", () => {
  it("4項目を正規化し、AND検索用の条件を保持する", () => {
    const search = parseSearchParams({
      title: "  本  ",
      publisher: "出版社",
      author_name: "著者",
      isbn: "978-4",
    });
    expect(search.errors).toEqual({});
    expect(search.criteria.toString()).toBe(
      "title=%E6%9C%AC&publisher=%E5%87%BA%E7%89%88%E7%A4%BE&author_name=%E8%91%97%E8%80%85&isbn=978-4",
    );
    expect(search.values.title).toBe("  本  ");
  });

  it("空検索、空白だけの項目、重複パラメーターを拒否する", () => {
    expect(
      parseSearchParams({ title: "", publisher: "", author_name: "", isbn: "" }).errors,
    ).toHaveProperty("criteria");
    expect(parseSearchParams({ title: "  ", isbn: "978-4" }).errors).toHaveProperty("title");
    expect(parseSearchParams({ title: ["a", "b"] }).errors).toHaveProperty("title");
  });

  it("検索語の長さを制限する", () => {
    expect(parseSearchParams({ title: "a".repeat(256) }).errors).toHaveProperty("title");
  });
});
