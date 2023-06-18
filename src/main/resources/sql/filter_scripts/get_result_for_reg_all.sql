select "all reg" as "reg", r1.name as "Attribute", @a3 from @a0 vr
                                                                         join build b on b.id = vr.id_build
                                                                         join @a1 r1 on r1.id = vr.id_r1
                                                                         join region r on r.id = b.id_region where r1.id in (@a2) and vr.id_r1 in (@a2) group by vr.id_r1