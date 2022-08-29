select vr_name, r1_name, r2_name, update_type,
       (select count(id) from vr_update_type_mapping where vr_type_id = @a0) as 'vr_count'
from vr_update_type_mapping where vr_type_id = @a0 and id between
    (select min(id) from vr_update_type_mapping where vr_type_id = @a0)+@a1 and
    (select min(id) from vr_update_type_mapping where vr_type_id = @a0)+@a2 order by id;