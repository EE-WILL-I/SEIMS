SELECT VR.id_build, R_1.name as 'Attribute',
       VR.val_r2_1 as 'Value'
FROM @a1 VR
         join @a2 R_1 on R_1.id = VR.id_r1
         join build b on b.id = VR.id_build join region r on r.id = b.id_region where r.id = @a0 GROUP BY R_1.id