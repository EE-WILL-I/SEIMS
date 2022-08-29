SELECT VR.id_build, R_1.name as 'показатель',
VR.val_r2_1 as 'значение'
FROM @a1 VR
    join @a2 R_1 on R_1.id = VR.id_r1
    where VR.id_build = @a0 GROUP BY R_1.id