SELECT VR9.id_buildokud, R9_1.name as 'показатель',
(SELECT sum(VRR9.value) FROM doo_VR9 VRR9 join buildokud bo on bo.id = VRR9.id_buildokud WHERE (VRR9.id_R9_1=VR9.id_R9_1 and bo.id_build = @a0)) as 'Всего работников',
(SELECT VRR9.value FROM doo_VR9 VRR9 join buildokud bo on bo.id = VRR9.id_buildokud WHERE (VRR9.id_R9_1=VR9.id_R9_1 and VRR9.id_R9_2=1 and bo.id_build = @a0)) as 'стаж работы до 3х лет',
(SELECT VRR9.value FROM doo_VR9 VRR9 join buildokud bo on bo.id = VRR9.id_buildokud WHERE (VRR9.id_R9_1=VR9.id_R9_1 and VRR9.id_R9_2=2 and bo.id_build = @a0)) as 'от 3 до 5',
(SELECT VRR9.value FROM doo_VR9 VRR9 join buildokud bo on bo.id = VRR9.id_buildokud WHERE (VRR9.id_R9_1=VR9.id_R9_1 and VRR9.id_R9_2=3 and bo.id_build = @a0)) as 'от 5 до 10',
(SELECT VRR9.value FROM doo_VR9 VRR9 join buildokud bo on bo.id = VRR9.id_buildokud WHERE (VRR9.id_R9_1=VR9.id_R9_1 and VRR9.id_R9_2=4 and bo.id_build = @a0)) as 'от 10 до 15',
(SELECT VRR9.value FROM doo_VR9 VRR9 join buildokud bo on bo.id = VRR9.id_buildokud WHERE (VRR9.id_R9_1=VR9.id_R9_1 and VRR9.id_R9_2=5 and bo.id_build = @a0)) as 'от 15 до 20',
(SELECT VRR9.value FROM doo_VR9 VRR9 join buildokud bo on bo.id = VRR9.id_buildokud WHERE (VRR9.id_R9_1=VR9.id_R9_1 and VRR9.id_R9_2=6 and bo.id_build = @a0)) as '20 и более лет' FROM doo_VR9 VR9
     join doo_R9_1 R9_1 on R9_1.id = VR9.id_R9_1
     join doo_R9_2 R9_2 on R9_2.id = VR9.id_R9_2
     join BuildOKUD bo on bo.id = VR9.id_buildokud
     join Build b on b.id = bo.id_build
     where b.id = @a0 GROUP BY R9_1.id