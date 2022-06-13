SELECT VR12_1.id_buildokud, R12_1.name as 'показатель', VR12_1.value as 'значение'
from doo_vr12_1 VR12_1
    join doo_r12_1 R12_1 on R12_1.id = VR12_1.id_r12_1
    JOIN buildokud bo on bo.id = VR12_1.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0