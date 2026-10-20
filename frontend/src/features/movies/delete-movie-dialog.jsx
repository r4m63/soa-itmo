import { useState } from "react"
import { toast } from "sonner"

import { deleteMovie } from "@/api/movie-service"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { notifyError } from "@/lib/api-errors"

/** Подтверждение DELETE /movies/{id}. */
export function DeleteMovieDialog({ movie, onOpenChange, onDeleted }) {
  const [deleting, setDeleting] = useState(false)

  const confirm = async (event) => {
    event.preventDefault()
    setDeleting(true)
    try {
      await deleteMovie(movie.id)
      toast.success("Фильм удалён", { description: movie.name })
      onDeleted(movie)
      onOpenChange(false)
    } catch (error) {
      notifyError(error)
    } finally {
      setDeleting(false)
    }
  }

  return (
    <AlertDialog open={movie != null} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Удалить фильм?</AlertDialogTitle>
          <AlertDialogDescription>
            «{movie?.name}» (ID {movie?.id}) будет удалён без возможности восстановления.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Отмена</AlertDialogCancel>
          <AlertDialogAction variant="destructive" disabled={deleting} onClick={confirm}>Удалить</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
