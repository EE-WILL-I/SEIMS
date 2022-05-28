SELECT dv.id_buildokud, dr.name, dv.value FROM doo_VR1 dv
    join BuildOKUD doo on doo.id = dv.id_buildokud
    join Build b on b.id = doo.id_build
    join doo_R1 dr on dr.id = dv.id_r1 where b.id = @a0
