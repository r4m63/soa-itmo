import { useState } from "react"
import { ArrowLeftRightIcon, ArrowRightIcon } from "lucide-react"
import { toast } from "sonner"

import { redistributeOscars } from "@/api/oscar-service"
import { EnumCombobox } from "@/components/enum-combobox"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Field, FieldError, FieldLabel } from "@/components/ui/field"
import { ApiError, notifyError, pathErrors } from "@/lib/api-errors"
import { enumLabel } from "@/lib/enums"
import { MOVIES_WORDS, OSCARS_WORDS, plural } from "@/lib/format"
import { ConfirmButton } from "./confirm-button"

/** POST /oscar/genres/redistribute-rewards/{from-genre}/{to-genre} */
export function RedistributeCard() {
  const [from, setFrom] = useState(undefined)
  const [to, setTo] = useState(undefined)
  const [running, setRunning] = useState(false)
  const [result, setResult] = useState(null)
  // ошибки сервиса по параметрам пути: { "from-genre": "...", "to-genre": "..." }
  const [fieldErrors, setFieldErrors] = useState({})

  const sameGenre = from && to && from === to
  const toError = fieldErrors["to-genre"] ?? (sameGenre ? "Жанр назначения должен отличаться от исходного" : null)

  const run = async () => {
    setRunning(true)
    setFieldErrors({})
    try {
      const response = await redistributeOscars(from, to)
      setResult({ ...response, from, to })
      toast.success("Оскары перераспределены")
    } catch (e) {
      // жанры с ошибкой подсвечиваются в полях, текст ошибки — в тосте
      if (e instanceof ApiError && e.status === 400) setFieldErrors(pathErrors(e))
      notifyError(e)
    } finally {
      setRunning(false)
    }
  }

  const choose = (setter) => (value) => {
    setter(value)
    setFieldErrors({})
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2"><ArrowLeftRightIcon className="size-5" />Перераспределить Оскары</CardTitle>
        <CardDescription>
          У фильмов исходного жанра изымаются все Оскары сверх одного и поровну делятся между фильмами целевого жанра.
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col gap-4 text-sm">
        <div className="grid items-start gap-3 sm:grid-cols-[1fr_auto_1fr]">
          <Field data-invalid={!!fieldErrors["from-genre"] || undefined}>
            <FieldLabel htmlFor="from-genre">Из жанра</FieldLabel>
            <EnumCombobox id="from-genre" dictionary="genres" value={from} invalid={!!fieldErrors["from-genre"]} onChange={choose(setFrom)} />
            <FieldError>{fieldErrors["from-genre"]}</FieldError>
          </Field>
          <Button
            variant="ghost"
            size="icon"
            className="mt-6 hidden sm:inline-flex"
            aria-label="Поменять жанры местами"
            onClick={() => {
              setFrom(to)
              setTo(from)
              setFieldErrors({})
            }}
          >
            <ArrowRightIcon />
          </Button>
          <Field data-invalid={!!toError || undefined}>
            <FieldLabel htmlFor="to-genre">В жанр</FieldLabel>
            <EnumCombobox id="to-genre" dictionary="genres" value={to} invalid={!!toError} onChange={choose(setTo)} />
            <FieldError>{toError}</FieldError>
          </Field>
        </div>
        {result && (
          <p>
            {result.sourceMovies} {plural(result.sourceMovies, MOVIES_WORDS)} жанра «{enumLabel("genres", result.from)}» передали{" "}
            <span className="font-semibold">{result.transferredOscars} {plural(result.transferredOscars, OSCARS_WORDS)}</span>{" "}
            {result.targetMovies} {plural(result.targetMovies, MOVIES_WORDS)} жанра «{enumLabel("genres", result.to)}».
          </p>
        )}
      </CardContent>
      <CardFooter>
        <ConfirmButton
          title="Перераспределить Оскары?"
          description={from && to
            ? `Оскары фильмов жанра «${enumLabel("genres", from)}» сверх одного перейдут фильмам жанра «${enumLabel("genres", to)}».`
            : ""}
          confirmLabel="Перераспределить"
          disabled={running || !from || !to || sameGenre}
          onConfirm={run}
        >
          {running ? "Перераспределение…" : "Перераспределить"}
        </ConfirmButton>
      </CardFooter>
    </Card>
  )
}
