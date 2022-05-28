SELECT vr3.id_buildokud, R3_1.name as 'показатель',
       (SELECT VRR3.value FROM doo_VR3 VRR3 join buildokud bo on bo.id = VRR3.id_buildokud WHERE (VRR3.id_R3_1=VR3.id_R3_1 and VRR3.id_R3_2=1 and bo.id_build = @a0)) as 'Численность воспитанников (всего)',
       (SELECT VRR3.value FROM doo_VR3 VRR3 join buildokud bo on bo.id = VRR3.id_buildokud WHERE (VRR3.id_R3_1=VR3.id_R3_1 and VRR3.id_R3_2=2 and bo.id_build = @a0)) as 'Число детей в возрасте 3 года и старше в группах',
       (SELECT VRR3.value FROM doo_VR3 VRR3 join buildokud bo on bo.id = VRR3.id_buildokud WHERE (VRR3.id_R3_1=VR3.id_R3_1 and VRR3.id_R3_2=3 and bo.id_build = @a0)) as 'Численность воспитанников с ограниченными возможностями здоровья',
       (SELECT VRR3.value FROM doo_VR3 VRR3 join buildokud bo on bo.id = VRR3.id_buildokud WHERE (VRR3.id_R3_1=VR3.id_R3_1 and VRR3.id_R3_2=4 and bo.id_build = @a0)) as 'Численность воспитанников с детьми-инвалидами',
       (SELECT VRR3.value FROM doo_VR3 VRR3 join buildokud bo on bo.id = VRR3.id_buildokud WHERE (VRR3.id_R3_1=VR3.id_R3_1 and VRR3.id_R3_2=5 and bo.id_build = @a0)) as 'Число групп (всего)',
       (SELECT VRR3.value FROM doo_VR3 VRR3 join buildokud bo on bo.id = VRR3.id_buildokud WHERE (VRR3.id_R3_1=VR3.id_R3_1 and VRR3.id_R3_2=6 and bo.id_build = @a0)) as 'Число групп в том числе для детей в возрасте 3 года и старше',
       (SELECT VRR3.value FROM doo_VR3 VRR3 join buildokud bo on bo.id = VRR3.id_buildokud WHERE (VRR3.id_R3_1=VR3.id_R3_1 and VRR3.id_R3_2=7 and bo.id_build = @a0)) as 'Число мест (всего)'
FROM doo_VR3 VR3
         join doo_R3_1 R3_1 ON R3_1.id = VR3.id_R3_1
         JOIN doo_R3_2 R3_2 on R3_2.id = VR3.id_R3_2
         join BuildOKUD bo on bo.id = VR3.id_buildokud
         join Build b on b.id = bo.id_build where b.id = @a0 GROUP BY R3_1.id;