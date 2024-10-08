CREATE TABLE IF NOT EXISTS TB_CATEGORIES (
     category_code BIGINT PRIMARY KEY,
     category_name VARCHAR(20)
);

INSERT INTO TB_CATEGORIES (category_code, category_name)
SELECT 1000, '식품'
    WHERE NOT EXISTS (SELECT 1 FROM TB_CATEGORIES WHERE category_code = 1000);

INSERT INTO TB_CATEGORIES (category_code, category_name)
SELECT 2000, '전자제품'
    WHERE NOT EXISTS (SELECT 1 FROM TB_CATEGORIES WHERE category_code = 2000);

INSERT INTO TB_CATEGORIES (category_code, category_name)
SELECT 3000, '의류'
    WHERE NOT EXISTS (SELECT 1 FROM TB_CATEGORIES WHERE category_code = 3000);