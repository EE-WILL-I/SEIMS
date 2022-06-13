SELECT VR2.id_buildokud, R2.name as 'показатель', VR2.value as 'значение' FROM doo_vr2 VR2
    join buildokud doo ON doo.id = VR2.id_buildokud
    join doo_r2 R2 on R2.id = VR2.id_r2
    join build b on b.id = doo.id_build where b.id = @a0