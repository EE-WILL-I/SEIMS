select r.name as "region", r1.name as "attribute", @a3 from @a1 r1 , @a0 vr
                                                                         join build b on b.id = vr.id_build
                                                                         join region r on r.id = b.id_region where r1.id in (@a2) and vr.id_r1 in (@a2) group by r1.id