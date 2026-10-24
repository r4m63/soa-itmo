import { useEffect, useState } from "react"

import { getMovie } from "@/api/movie-service"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { Sheet, SheetContent, SheetDescription, SheetFooter, SheetHeader, SheetTitle } from "@/components/ui/sheet"
import { Skeleton } from "@/components/ui/skeleton"
import { ApiError, notifyError } from "@/lib/api-errors"
import { enumLabel } from "@/lib/enums"
import { formatDate, OSCARS_WORDS, plural } from "@/lib/format"

export function MovieDetailsSheet({ movieId, onOpenChange, onEdit, onDelete }) {
  const [movie, setMovie] = useState(null)
  const [notFound, setNotFound] = useState(false)

  useEffect(() => {
    if (movieId == null) return
    let cancelled = false
    setMovie(null)
    setNotFound(false)
    getMovie(movieId)
      .then((m) => !cancelled && setMovie(m))
      .catch((error) => {
        if (cancelled) return
        if (error instanceof ApiError && error.status === 404) setNotFound(true)
        notifyError(error)
      })
    return () => {
      cancelled = true
    }
  }, [movieId])

  return (
    <Sheet open={movieId != null} onOpenChange={onOpenChange}>
      <SheetContent className="flex flex-col gap-0 sm:max-w-md">
        <SheetHeader>
          <SheetTitle>{movie?.name ?? (notFound ? "Фильм не найден" : "Загрузка…")}</SheetTitle>
          <SheetDescription>ID {movieId}</SheetDescription>
        </SheetHeader>
        <div className="flex flex-1 flex-col gap-4 overflow-y-auto px-4 text-sm">
          {!movie && !notFound && <Skeleton className="h-24 w-full" />}
          {notFound && <p className="text-muted-foreground">Фильм удалён или ещё не создан.</p>}
          {movie && (
            <>
              <p className="leading-relaxed">{describeMovie(movie)}</p>
              <Separator />
              <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2">
                <Item label="Дата создания" value={formatDate(movie.creationDate)} />
                <Item label="Координаты" value={`X ${movie.coordinates.x}, Y ${movie.coordinates.y}`} />
                <Item label="Оскары" value={movie.oscarsCount} />
                <Item label="Жанр" value={enumLabel("genres", movie.genre) ?? "—"} />
                <Item label="Рейтинг MPAA" value={enumLabel("mpaa-ratings", movie.mpaaRating)} />
              </dl>
              <Separator />
              <p className="font-medium">Сценарист</p>
              {movie.screenwriter ? (
                <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2">
                  <Item label="Имя" value={movie.screenwriter.name} />
                  <Item label="Паспорт" value={movie.screenwriter.passportID ?? "—"} />
                  <Item label="Цвет волос" value={enumLabel("colors", movie.screenwriter.hairColor) ?? "—"} />
                  <Item label="Национальность" value={enumLabel("countries", movie.screenwriter.nationality)} />
                </dl>
              ) : (
                <p className="text-muted-foreground">Не указан</p>
              )}
            </>
          )}
        </div>
        <SheetFooter className="flex-row justify-end">
          <Button variant="outline" disabled={!movie} onClick={() => onEdit(movie)}>Изменить</Button>
          <Button variant="destructive" disabled={!movie} onClick={() => onDelete(movie)}>Удалить</Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  )
}

function Item({ label, value }) {
  return (
    <>
      <dt className="text-muted-foreground">{label}</dt>
      <dd>{value}</dd>
    </>
  )
}

/** Описание фильма одним абзацем. */
export function describeMovie(movie) {
  const genre = movie.genre ? enumLabel("genres", movie.genre).toLowerCase() : "фильм без указанного жанра"
  const parts = [
    `«${movie.name}» — ${genre} с рейтингом ${enumLabel("mpaa-ratings", movie.mpaaRating)}, добавлен ${formatDate(movie.creationDate)}.`,
    `${movie.oscarsCount} ${plural(movie.oscarsCount, OSCARS_WORDS)}.`,
    `Координаты: (${movie.coordinates.x}; ${movie.coordinates.y}).`,
  ]
  const sw = movie.screenwriter
  if (sw) {
    const details = [
      sw.passportID && `паспорт ${sw.passportID}`,
      sw.hairColor && `цвет волос — ${enumLabel("colors", sw.hairColor).toLowerCase()}`,
    ].filter(Boolean)
    parts.push(`Сценарист: ${sw.name} (${enumLabel("countries", sw.nationality)})${details.length ? ", " + details.join(", ") : ""}.`)
  } else {
    parts.push("Сценарист не указан.")
  }
  return parts.join(" ")
}
