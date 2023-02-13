select distinct b.name, r1.name, @a3 from @a1 r1 , @a0 vr
 join build b on b.id = vr.id_build where r1.id in (@a2) and vr.id_r1 in (@a2) and b.id_region = @a4 and b.id in (@a5);