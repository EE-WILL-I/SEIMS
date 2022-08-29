SELECT VR.id_build, R_1.name as 'показатель',
@a1
FROM @a2 VR
    join @a3 R_1 on R_1.id = VR.id_r1
    where VR.id_build = @a0 GROUP BY R_1.id