update @a4 vr
    join @a5 r1 on r1.id = vr.id_r1
    join build b on b.id = vr.id_build
    set vr.@a2 = @a3 where b.id = @a0 and r1.name like '@a1'