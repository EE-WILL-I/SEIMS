SELECT  VR8.id_buildokud, R7_1.name as 'показатель',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=1 and bo.id_build = @a0)) as 'моложе 25 лет',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=2 and bo.id_build = @a0)) as '25-29',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=3 and bo.id_build = @a0)) as '30-34',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=4 and bo.id_build = @a0)) as '35-39',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=5 and bo.id_build = @a0)) as '40-44',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=6 and bo.id_build = @a0)) as '45-49',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=7 and bo.id_build = @a0)) as '50-54',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=8 and bo.id_build = @a0)) as '55-59',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=9 and bo.id_build = @a0)) as '60-64',
(SELECT VRR8.value FROM doo_VR8 VRR8 join buildokud bo on bo.id = VRR8.id_buildokud WHERE (VRR8.id_r7_1=VR8.id_r7_1 and VRR8.id_r8=10 and bo.id_build = @a0)) as '65 и старше'
FROM doo_VR8 VR8
    join doo_R8 R8 on R8.id = VR8.id_r8
    join doo_R7_1 R7_1 on R7_1.id = VR8.id_r7_1
    join BuildOKUD bo on bo.id = VR8.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0 GROUP BY R7_1.id