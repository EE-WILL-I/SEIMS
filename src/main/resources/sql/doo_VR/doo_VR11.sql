SELECT VR11.id_buildokud, R11.name as 'показатель', vr11.value as 'значение' FROM doo_vr11 VR11
    join doo_r11 R11 on R11.id = VR11.id_r11
    JOIN buildokud bo on bo.id = VR11.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0 GROUP BY R11.id