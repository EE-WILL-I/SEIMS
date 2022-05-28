SELECT sum(VR6.value) as "Всего воспитанников", 
(SELECT sum(VR6.value) FROM doo_VR6 VR6 join doo_R6 R6 on R6.id = VR6.id_r6 WHERE R6.value = 1) as 'Всего воспитанников, говорящих на языках народов России'
FROM doo_VR6 VR6
    join doo_R6 R6 on R6.id = VR6.id_r6