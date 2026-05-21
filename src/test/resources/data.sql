INSERT INTO theme (id, name, description, thumbnail_url)
VALUES
    (1, '잠실 미스터리', '사라진 열쇠를 찾아 탈출하는 클래식 테마', 'https://example.com/theme-jamsil.jpg'),
    (2, '북촌의 밤', '한옥 골목에서 벌어지는 추리 테마', 'https://example.com/theme-bukchon.jpg'),
    (3, '우주 정거장', '정전된 정거장에서 귀환 코드를 찾는 SF 테마', 'https://example.com/theme-space.jpg');

INSERT INTO reservation (id, name, date, time, theme_id)
VALUES
    (1, '브라운', '2026-05-14', '10:00', 1),
    (2, '포비', '2026-05-14', '12:00', 1),
    (3, '라이언', '2026-05-15', '14:00', 1),
    (4, '춘식', '2026-05-16', '16:00', 2),
    (5, '무지', '2026-05-17', '18:00', 2),
    (6, '콘', '2026-05-18', '20:00', 3),
    (7, '네오', '2026-05-20', '22:00', 1),
    (8, '프로도', '2026-05-08', '10:00', 2);
