SELECT VR15.id_buildokud, R15.name as 'показатель', VR15.value as 'значение'
from doo_vr15 VR15
    join doo_r15 R15 on R15.id = VR15.id_r15
    JOIN buildokud bo on bo.id = VR15.id_buildokud
    join build b on b.id = bo.id_build
    where b.id = @a0