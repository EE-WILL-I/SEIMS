SELECT VR13_1.id_buildokud, R13_1.name as 'показатель', VR13_1.value as 'значение'
from doo_vr13_1 VR13_1
    join doo_r13_1 R13_1 on R13_1.id = VR13_1.id_r13_1
    JOIN buildokud bo on bo.id = VR13_1.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0