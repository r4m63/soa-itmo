import { useEffect, useState } from "react"
import { toast } from "sonner"

import { deleteMoviesByGenre } from "@/api/movie-service"
import { EnumCombobox } from "@/components/enum-combobox"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Field, FieldDescription, FieldError, FieldLabel } from "@/components/ui/field"
import { notifyError } from "@/lib/api-errors"
import { enumLabel } from "@/lib/enums"
import { MOVIES_WORDS, plural } from "@/lib/format"

/** DELETE /movies/by-genre/{genre}: выбор жанра и показ числа удалённых фильмов. */
export function DeleteByGenreDialog({ open, onOpenChange, onDeleted }) {
  const [genre, setGenre] = useState(undefined)
  const [error, setError] = useState(null)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    if (open) {
      setGenre(undefined)
      setError(null)
    }
  }, [open])

  const confirm = async () => {
    if (!genre) {
      setError("Выберите жанр")
      return
    }
    setDeleting(true)
    try {
      const { deletedCount } = await deleteMoviesByGenre(genre)
      toast.success(`Удалено ${deletedCount} ${plural(deletedCount, MOVIES_WORDS)}`, {
        description: `Жанр «${enumLabel("genres", genre)}»`,
      })
      onDeleted()
      onOpenChange(false)
    } catch (e) {
      notifyError(e)
    } finally {
      setDeleting(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Удалить фильмы жанра</DialogTitle>
          <DialogDescription>Будут удалены все фильмы выбранного жанра. Фильмы без жанра останутся.</DialogDescription>
        </DialogHeader>
        <Field data-invalid={!!error || undefined}>
          <FieldLabel htmlFor="delete-genre">Жанр</FieldLabel>
          <EnumCombobox id="delete-genre" dictionary="genres" value={genre} invalid={!!error}
                        onChange={(v) => { setGenre(v); setError(null) }} />
          <FieldDescription>Операцию нельзя отменить.</FieldDescription>
          <FieldError>{error}</FieldError>
        </Field>
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>Отмена</Button>
          <Button variant="destructive" disabled={deleting} onClick={confirm}>Удалить</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
