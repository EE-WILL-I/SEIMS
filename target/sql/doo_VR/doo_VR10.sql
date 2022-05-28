SELECT VR10.id_buildokud, R10_1.name as 'показатель',
(SELECT VRR10.value FROM doo_VR10 VRR10 join buildokud bo on bo.id = VRR10.id_buildokud WHERE (VRR10.id_R10_1=VR10.id_R10_1 and VRR10.id_R10_2=1 and bo.id_build = @a0)) as 'Общая площадь зданий (помещений)',
(SELECT VRR10.value FROM doo_VR10 VRR10 join buildokud bo on bo.id = VRR10.id_buildokud WHERE (VRR10.id_R10_1=VR10.id_R10_1 and VRR10.id_R10_2=2 and bo.id_build = @a0)) as 'Площадь на правах собственности',
(SELECT VRR10.value FROM doo_VR10 VRR10 join buildokud bo on bo.id = VRR10.id_buildokud WHERE (VRR10.id_R10_1=VR10.id_R10_1 and VRR10.id_R10_2=3 and bo.id_build = @a0)) as 'в оперативном управлении',
(SELECT VRR10.value FROM doo_VR10 VRR10 join buildokud bo on bo.id = VRR10.id_buildokud WHERE (VRR10.id_R10_1=VR10.id_R10_1 and VRR10.id_R10_2=4 and bo.id_build = @a0)) as 'арендованная',
(SELECT VRR10.value FROM doo_VR10 VRR10 join buildokud bo on bo.id = VRR10.id_buildokud WHERE (VRR10.id_R10_1=VR10.id_R10_1 and VRR10.id_R10_2=5 and bo.id_build = @a0)) as 'другие формы владения',
(SELECT VRR10.value FROM doo_VR10 VRR10 join buildokud bo on bo.id = VRR10.id_buildokud WHERE (VRR10.id_R10_1=VR10.id_R10_1 and VRR10.id_R10_2=6 and bo.id_build = @a0)) as 'Из общей площади - площадь, сданная в аренду (субаренду)'
FROM doo_VR10 VR10
    join doo_R10_1 R10_1 on R10_1.id = VR10.id_R10_1
    join doo_R10_2 R10_2 on R10_2.id = VR10.id_R10_2
    join BuildOKUD bo ON bo.id = VR10.id_buildokud
    join Build b on b.id = bo.id_build
    where b.id = @a0 GROUP BY R10_1.id