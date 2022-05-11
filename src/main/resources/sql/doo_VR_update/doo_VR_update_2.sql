update doo_vr@a3 vr1
    join doo_r@a3 r1 on r1.id = vr1.id_R@a3
    join buildokud bo on bo.id_build = vr@a3.id_buildokud
    join build b on b.id = bo.id_build
    join organizations org on org.id_build = b.id set value = @a2 where org.id = @a0 and r1.name like '@a1'
