SELECT VR15.id_buildokud, R15.name as 'показатель', VR15.value as 'значение'
from doo_VR15 VR15
    join doo_R15 R15 on R15.id = VR15.id_R15
    JOIN BuildOKUD bo on bo.id = VR15.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0