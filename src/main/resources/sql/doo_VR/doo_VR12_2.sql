SELECT VR12_2.id_buildokud, R12_2.name as 'показатель', VR12_2.value as 'значение'
from doo_vr12_2 VR12_2
    join doo_r12_2 R12_2 on R12_2.id = VR12_2.id_r12_2
    JOIN buildokud bo on bo.id = VR12_2.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0