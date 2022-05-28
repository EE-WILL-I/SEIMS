update doo_vr@a3 vr1
    join doo_r@a3 r1 on r1.id = vr1.id_R@a3
    join buildokud bo on bo.id = vr1.id_buildokud
    join build b on b.id = bo.id_build set value = @a2 where b.id = @a0 and r1.name like '@a1'
