SELECT VR14.id_buildokud, R14.name as 'показатель', VR14.value as 'значение'
from doo_VR14 VR14
    join doo_R14 R14 on R14.id = VR14.id_R14
    JOIN BuildOKUD bo on bo.id = VR14.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0