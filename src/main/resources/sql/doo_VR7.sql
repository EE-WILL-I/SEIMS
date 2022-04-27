SELECT b.id, org.name as 'Организация', R7_1.name as 'Должность',
(SELECT VRR7.value FROM doo_VR7 VRR7 WHERE (VRR7.id_r7_1 = VR7.id_r7_1 and VRR7.id_r7_2=1)) as 'Всего работников',
(SELECT VRR7.value FROM doo_VR7 VRR7 WHERE (VRR7.id_r7_1 = VR7.id_r7_1 and VRR7.id_r7_2=2)) as 'Работники с высшем образованием',
(SELECT VRR7.value FROM doo_VR7 VRR7 WHERE (VRR7.id_r7_1 = VR7.id_r7_1 and VRR7.id_r7_2=3)) as 'Работники с высшим педогогическим образованием',
(SELECT VRR7.value FROM doo_VR7 VRR7 WHERE (VRR7.id_r7_1 = VR7.id_r7_1 and VRR7.id_r7_2=4)) as 'Работники со средним профессиональным образованием по программам подготовки специалистов среднего звена',
(SELECT VRR7.value FROM doo_VR7 VRR7 WHERE (VRR7.id_r7_1 = VR7.id_r7_1 and VRR7.id_r7_2=5)) as 'Работники со средним профессиональным педогогическим образованием по программам подготовки специалистов среднего звена',
(SELECT VRR7.value FROM doo_VR7 VRR7 WHERE (VRR7.id_r7_1 = VR7.id_r7_1 and VRR7.id_r7_2=6)) as 'Всего работников (женщин)',
(SELECT VRR7.value FROM doo_VR7 VRR7 WHERE (VRR7.id_r7_1 = VR7.id_r7_1 and VRR7.id_r7_2=7)) as 'Численность внешних совместителей'
FROM doo_VR7 VR7 join doo_R7_1 R7_1 on R7_1.id = VR7.id_r7_1 join doo_R7_2 R7_2 on R7_2.id = VR7.id_r7_2 join BuildOKUD bo on bo.id = VR7.id_buildokud join Build b on b.id = bo.id_build join organizations org on org.id_build = b.id where org.id = @a0 GROUP BY R7_1.id ;