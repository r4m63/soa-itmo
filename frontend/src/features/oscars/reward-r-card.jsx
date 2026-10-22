import { useState } from "react"
import { AwardIcon } from "lucide-react"
import { toast } from "sonner"

import { rewardRRatedMovies } from "@/api/oscar-service"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { notifyError } from "@/lib/api-errors"
import { MOVIES_WORDS, plural } from "@/lib/format"
import { ConfirmButton } from "./confirm-button"

/** POST /oscar/movies/reward-r */
export function RewardRCard() {
  const [running, setRunning] = useState(false)
  const [result, setResult] = useState(null)

  const run = async () => {
    setRunning(true)
    try {
      const { affectedMovies } = await rewardRRatedMovies()
      setResult({ affectedMovies, at: new Date() })
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
      <CardContent className="flex flex-col gap-3 text-sm">
        {result && (
          <p>
            Награждено <span className="font-semibold">{result.affectedMovies}</span>{" "}
            {plural(result.affectedMovies, MOVIES_WORDS)} рейтинга R
            <span className="text-muted-foreground"> · {result.at.toLocaleTimeString("ru-RU")}</span>
          </p>
        )}
        {!result && <p className="text-muted-foreground">Операцию ещё не запускали.</p>}
      </CardContent>
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
