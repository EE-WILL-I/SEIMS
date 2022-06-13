SELECT dv.id_buildokud, dr.name, dv.value FROM doo_vr1 dv
    join buildokud doo on doo.id = dv.id_buildokud
    join build b on b.id = doo.id_build
    join doo_r1 dr on dr.id = dv.id_r1 where b.id = @a0
