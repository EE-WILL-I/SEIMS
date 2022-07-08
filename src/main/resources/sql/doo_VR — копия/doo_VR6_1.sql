SELECT VR6.id_buildokud, R6.name as 'показатель', VR6.value as 'значение' FROM doo_VR6 VR6
    join doo_R6 R6 on R6.id = VR6.id_r6
    join BuildOKUD bo on bo.id = VR6.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0 GROUP by R6.id