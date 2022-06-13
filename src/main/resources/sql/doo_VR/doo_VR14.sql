SELECT VR14.id_buildokud, R14.name as 'показатель', VR14.value as 'значение'
from doo_vr14 VR14
    join doo_r14 R14 on R14.id = VR14.id_r14
    JOIN buildokud bo on bo.id = VR14.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0