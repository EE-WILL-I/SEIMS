SELECT VR6.id_buildokud, R6.name as 'показатель', VR6.value as 'значение' FROM doo_vr6 VR6
    join doo_r6 R6 on R6.id = VR6.id_r6
    join buildokud bo on bo.id = VR6.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0 GROUP by R6.id