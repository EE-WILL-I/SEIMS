select  b.name as "Организация", r1.name as "Поле", @a3 from @a1 r1 , @a0 vr
 join build b on b.id = vr.id_build where r1.id in (@a2) and vr.id_r1 in (@a2) group by r1.id