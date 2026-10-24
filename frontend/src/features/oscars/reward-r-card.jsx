import { useState } from "react"
import { AwardIcon } from "lucide-react"
import { toast } from "sonner"

import { rewardRRatedMovies } from "@/api/oscar-service"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { notifyError } from "@/lib/api-errors"
import { MOVIES_WORDS, plural } from "@/lib/format"
import { ConfirmButton } from "./confirm-button"

export function RewardRCard() {
  const [running, setRunning] = useState(false)

  const run = async () => {
    setRunning(true)
    try {
      const { affectedMovies } = await rewardRRatedMovies()
      toast.success(`Награждено ${affectedMovies} ${plural(affectedMovies, MOVIES_WORDS)}`)
    } catch (e) {
      notifyError(e)
    } finally {
      setRunning(false)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2"><AwardIcon className="size-5" />Наградить фильмы рейтинга R</CardTitle>
        <CardDescription>Каждый фильм с рейтингом MPAA «R» получит ещё один Оскар.</CardDescription>
      </CardHeader>
      <CardContent className="flex-1" />
      <CardFooter>
        <ConfirmButton
          title="Наградить фильмы рейтинга R?"
          description="Всем фильмам с рейтингом R прибавится по одному Оскару. Операцию нельзя отменить."
          confirmLabel="Наградить"
          disabled={running}
          onConfirm={run}
        >
          {running ? "Награждение…" : "Наградить"}
        </ConfirmButton>
      </CardFooter>
    </Card>
  )
}
