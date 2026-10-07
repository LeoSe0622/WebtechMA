-- Selbst eingetippte Produkte gehören dem Nutzer, der sie angelegt hat, und sind nur für ihn sichtbar.
-- Katalog- und Open-Food-Facts-Produkte bleiben gemeinsam (created_by IS NULL). Review 02, B1 und M3.
ALTER TABLE product
    ADD COLUMN created_by BIGINT REFERENCES app_user (id) ON DELETE CASCADE;

CREATE INDEX idx_product_created_by ON product (created_by);
