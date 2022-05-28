update doo_vr@a4 vr3
    join doo_r@a4_1 r1 on r1.id = vr3.id_R@a4_1
    join doo_r@a4_2 r2 on r2.id = vr3.id_R@a4_2
    join buildokud bo on bo.id = vr3.id_buildokud
    join build b on b.id = bo.id_build set value = @a3 where b.id = @a0 and r1.name like '@a1' and r2.name like '@a2'