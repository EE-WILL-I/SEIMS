SELECT VR.id_build, R_1.name as 'Attribute',
@a1
 FROM @a2 VR
    join @a3 R_1 on R_1.id = VR.id_r1
     join build b on b.id = VR.id_build
     join region r on r.id = b.id_region where r.id = @a0 GROUP BY R_1.id