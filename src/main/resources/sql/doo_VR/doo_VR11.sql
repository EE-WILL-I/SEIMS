SELECT VR11.id_buildokud, R11.name as 'показатель', vr11.value as 'значение' FROM doo_VR11 VR11
    join doo_R11 R11 on R11.id = VR11.id_R11
    JOIN BuildOKUD bo on bo.id = VR11.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0 GROUP BY R11.id