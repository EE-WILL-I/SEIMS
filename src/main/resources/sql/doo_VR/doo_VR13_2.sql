SELECT VR13_2.id_buildokud, R13_2.name as 'показатель', VR13_2.value as 'значение'
from doo_vr13_2 VR13_2
    join doo_r13_2 R13_2 on R13_2.id = VR13_2.id_r13_2
    JOIN buildokud bo on bo.id = VR13_2.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0