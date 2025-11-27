package com.itsci.mju.maebanjumpen.housekeeperskill.dto

import com.itsci.mju.maebanjumpen.hire.dto.HireDTO
import com.itsci.mju.maebanjumpen.partyrole.dto.HousekeeperDTO
import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO

class HousekeeperDetailDTO : HousekeeperDTO() {
    var hires: List<HireDTO>? = null
    var jobsCompleted: Int = 0 // รับจำนวนงานที่เสร็จสิ้น
    var reviews: List<ReviewDTO>? = null // รับรายการรีวิว
}

