SELECT VR2.id_buildokud, R2.name as 'показатель', VR2.value as 'значение' FROM doo_VR2 VR2
    join BuildOKUD doo ON doo.id = VR2.id_buildokud
    join doo_R2 R2 on R2.id = VR2.id_R2
    join Build b on b.id = doo.id_build where b.id = @a0