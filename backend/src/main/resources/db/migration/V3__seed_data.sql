-- V3__seed_data.sql
-- Seed categories only. Admin user is provisioned at startup by AdminPasswordValidator.

INSERT INTO categories (name, description) VALUES
    ('Photography',      'Professional wedding photography services'),
    ('Videography',      'Wedding videography and cinematic films'),
    ('Catering',         'Food and beverage services for weddings'),
    ('Decoration',       'Venue decoration and floral arrangements'),
    ('Music & DJ',       'Live music, DJ, and sound systems'),
    ('Mehendi',          'Traditional mehendi / henna artists'),
    ('Bridal Makeup',    'Bridal and family makeup artists'),
    ('Wedding Venue',    'Wedding halls, farmhouses, and banquet halls'),
    ('Pandit / Priest',  'Vedic priests and wedding ceremony officiants'),
    ('Transportation',   'Wedding car rentals and guest transportation')
ON CONFLICT (name) DO NOTHING;
