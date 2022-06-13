SELECT VR5.id_buildokud, R5.name as 'показатель', VR5.value as 'значение 1',
(select VVR5.val3 from doo_vr5 VVR5 join buildokud bo on bo.id = VVR5.id_buildokud where (VVR5.id_r5 = 1 and bo.id_build = @a0)) as 'из них воспитанники в возрасте 3 года и старше'
FROM doo_vr5 VR5
    join buildokud bo on bo.id = VR5.id_buildokud
    join build b on b.id = bo.id_build
    join doo_r5 R5 on R5.id = VR5.id_r5
    where b.id = @a0