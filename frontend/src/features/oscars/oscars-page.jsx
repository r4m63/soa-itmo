import { RedistributeCard } from "./redistribute-card"
import { RewardRCard } from "./reward-r-card"

/** Раздел «Оскары»: операции Oscar Service. Сам Oscar Service читает и меняет фильмы через Movie Service. */
export function OscarsPage() {
  return (
    <div className="grid gap-4 px-4 py-4 md:py-6 lg:grid-cols-2 lg:px-6">
      <RewardRCard />
      <RedistributeCard />
    </div>
  )
}
