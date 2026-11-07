import {useEffect, useState} from "react"
import {XIcon} from "lucide-react"

import {getMovie} from "@/api/movie-service"
import {TruncatedText} from "@/components/truncated-text"
import {Button} from "@/components/ui/button"
import {Separator} from "@/components/ui/separator"
import {
    Sheet,
    SheetClose,
    SheetContent,
    SheetDescription,
    SheetFooter,
    SheetHeader,
    SheetTitle
} from "@/components/ui/sheet"
import {Skeleton} from "@/components/ui/skeleton"
import {ApiError, notifyError} from "@/lib/api-errors"
import {enumLabel} from "@/lib/enums"
import {formatDate} from "@/lib/format"

export function MovieDetailsSheet({movieId, onOpenChange, onEdit, onDelete}) {
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
            <SheetContent showCloseButton={false} className="flex flex-col gap-0 overflow-hidden sm:max-w-md">
                <div className="flex shrink-0 justify-end border-b p-2">
                    <SheetClose asChild>
                        <Button variant="ghost" size="icon-sm" aria-label="Закрыть">
                            <XIcon/>
                        </Button>
                    </SheetClose>
                </div>
                <div className="flex min-h-0 flex-1 flex-col overflow-y-auto">
                    <SheetHeader className="shrink-0">
                        <SheetTitle className="break-words">
                            {movie?.name ?? (notFound ? "Фильм не найден" : "Загрузка…")}
                        </SheetTitle>
                        <SheetDescription>ID {movieId}</SheetDescription>
                    </SheetHeader>
                    <div className="flex shrink-0 flex-col gap-4 px-4 text-sm">
                        {!movie && !notFound && <Skeleton className="h-24 w-full"/>}
                        {notFound && <p className="text-muted-foreground">Фильм удалён или ещё не создан.</p>}
                        {movie && (
                            <>
                                <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2">
                                    <Item label="Дата создания" value={formatDate(movie.creationDate)}/>
                                    <Item label="Координаты"
                                          value={`X ${movie.coordinates.x}, Y ${movie.coordinates.y}`}/>
                                    <Item label="Оскары" value={movie.oscarsCount}/>
                                    <Item label="Жанр" value={enumLabel("genres", movie.genre) ?? "—"}/>
                                    <Item label="Рейтинг MPAA" value={enumLabel("mpaa-ratings", movie.mpaaRating)}/>
                                </dl>
                                <Separator/>
                                <p className="font-medium">Сценарист</p>
                                {movie.screenwriter ? (
                                    <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2">
                                        <Item label="Имя" value={<TruncatedText value={movie.screenwriter.name}/>}/>
                                        <Item label="Паспорт" value={movie.screenwriter.passportID
                                            ? <TruncatedText value={movie.screenwriter.passportID}/>
                                            : "—"}/>
                                        <Item label="Цвет волос"
                                              value={enumLabel("colors", movie.screenwriter.hairColor) ?? "—"}/>
                                        <Item label="Национальность"
                                              value={enumLabel("countries", movie.screenwriter.nationality)}/>
                                    </dl>
                                ) : (
                                    <p className="text-muted-foreground">Не указан</p>
                                )}
                            </>
                        )}
                    </div>
                    <SheetFooter className="shrink-0 flex-row justify-end">
                        <Button variant="outline" disabled={!movie} onClick={() => onEdit(movie)}>Изменить</Button>
                        <Button variant="destructive" disabled={!movie} onClick={() => onDelete(movie)}>Удалить</Button>
                    </SheetFooter>
                </div>
            </SheetContent>
        </Sheet>
    )
}

function Item({label, value}) {
    return (
        <>
            <dt className="text-muted-foreground">{label}</dt>
            <dd className="min-w-0 break-words">{value}</dd>
        </>
    )
}
