SELECT VR13_1.id_buildokud, R13_1.name as 'показатель', VR13_1.value as 'значение'
from doo_VR13_1 VR13_1
    join doo_R13_1 R13_1 on R13_1.id = VR13_1.id_R13_1
    JOIN BuildOKUD bo on bo.id = VR13_1.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0