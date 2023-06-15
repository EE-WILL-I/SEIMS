select  "По всем организациям" as "Организация", r1.name as "Поле", @a3 from @a0 vr
 join build b on b.id = vr.id_build
  join @a1 r1 on r1.id = vr.id_r1
  where r1.id in (@a2) and vr.id_r1 in (@a2) group by vr.id_r1