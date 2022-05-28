SELECT VR12_1.id_buildokud, R12_1.name as 'показатель', VR12_1.value as 'значение'
from doo_VR12_1 VR12_1
    join doo_R12_1 R12_1 on R12_1.id = VR12_1.id_R12_1
    JOIN BuildOKUD bo on bo.id = VR12_1.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0