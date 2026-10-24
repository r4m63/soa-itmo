import { useEffect, useState } from "react"
import { toast } from "sonner"

import { createMovie, patchMovie } from "@/api/movie-service"
import { EnumCombobox } from "@/components/enum-combobox"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Field, FieldError, FieldGroup, FieldLabel, FieldLegend, FieldSet } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { ApiError, bodyFieldErrors, bodyGeneralErrors, notifyError } from "@/lib/api-errors"
import { diffPatch, emptyForm, formFromMovie, toMovieInput, validateForm } from "./movie-form"

export function MovieFormDialog({ open, movie, onOpenChange, onSaved }) {
  const editing = movie != null
  const [form, setForm] = useState(emptyForm)
  const [errors, setErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!open) return
    setForm(editing ? formFromMovie(movie) : emptyForm())
    setErrors({})
  }, [open, editing, movie])

  const set = (key, value) => setForm((f) => ({ ...f, [key]: value }))
  const setSw = (key, value) => setForm((f) => ({ ...f, screenwriter: { ...f.screenwriter, [key]: value } }))

  const submit = async (event) => {
    event.preventDefault()
    const clientErrors = validateForm(form)
    setErrors(clientErrors)
    if (Object.keys(clientErrors).length) return

    const input = toMovieInput(form)
    const patch = editing ? diffPatch(movie, input) : null
    if (editing && Object.keys(patch).length === 0) {
      toast.warning("Изменений нет")
      onOpenChange(false)
      return
    }

    setSubmitting(true)
    try {
      const saved = editing ? await patchMovie(movie.id, patch) : await createMovie(input)
      toast.success(editing ? "Фильм изменён" : "Фильм добавлен", { description: saved.name })
      onSaved(saved)
      onOpenChange(false)
    } catch (error) {
      if (error instanceof ApiError && error.status === 400) setErrors(bodyFieldErrors(error))
      notifyError(error, bodyGeneralErrors(error))
    } finally {
      setSubmitting(false)
    }
  }

  const text = (key, label, props = {}) => (
    <Field data-invalid={!!errors[key] || undefined}>
      <FieldLabel htmlFor={key}>{label}</FieldLabel>
      <Input id={key} aria-invalid={!!errors[key] || undefined} {...props} />
      <FieldError>{errors[key]}</FieldError>
    </Field>
  )

  const select = (key, label, combobox) => (
    <Field data-invalid={!!errors[key] || undefined}>
      <FieldLabel htmlFor={key}>{label}</FieldLabel>
      {combobox}
      <FieldError>{errors[key]}</FieldError>
    </Field>
  )

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-xl">
        <form onSubmit={submit} noValidate className="flex flex-col gap-6">
          <DialogHeader>
            <DialogTitle>{editing ? "Изменить фильм" : "Новый фильм"}</DialogTitle>
            <DialogDescription>
              {editing
                ? `ID ${movie.id}. Отправятся только изменённые поля.`
                : "ID и дату создания назначит сервис."}
            </DialogDescription>
          </DialogHeader>

          <FieldGroup>
            {text("name", "Название", { value: form.name, onChange: (e) => set("name", e.target.value) })}
            <div className="grid grid-cols-3 gap-4">
              {text("coordinates.x", "Координата X", { inputMode: "numeric", value: form.x, onChange: (e) => set("x", e.target.value) })}
              {text("coordinates.y", "Координата Y", { inputMode: "numeric", value: form.y, onChange: (e) => set("y", e.target.value) })}
              {text("oscarsCount", "Оскары", { type: "number", min: 1, step: 1, value: form.oscarsCount, onChange: (e) => set("oscarsCount", e.target.value) })}
            </div>
            <div className="grid grid-cols-2 gap-4">
              {select("genre", "Жанр", (
                <EnumCombobox id="genre" dictionary="genres" nullable nullLabel="Без жанра" value={form.genre}
                              invalid={!!errors.genre} onChange={(v) => set("genre", v)} />
              ))}
              {select("mpaaRating", "Рейтинг MPAA", (
                <EnumCombobox id="mpaaRating" dictionary="mpaa-ratings" value={form.mpaaRating}
                              invalid={!!errors.mpaaRating} onChange={(v) => set("mpaaRating", v)} />
              ))}
            </div>

            <FieldSet>
              <FieldLegend variant="label" className="flex items-center gap-2">
                <Checkbox
                  id="hasScreenwriter"
                  checked={form.hasScreenwriter}
                  onCheckedChange={(v) => set("hasScreenwriter", !!v)}
                />
                <label htmlFor="hasScreenwriter">Есть сценарист</label>
              </FieldLegend>
              {form.hasScreenwriter && (
                <FieldGroup className="grid grid-cols-2 gap-4">
                  {text("screenwriter.name", "Имя", { value: form.screenwriter.name, onChange: (e) => setSw("name", e.target.value) })}
                  {text("screenwriter.passportID", "Паспорт (не короче 5 символов)", { value: form.screenwriter.passportID, onChange: (e) => setSw("passportID", e.target.value) })}
                  {select("screenwriter.hairColor", "Цвет волос", (
                    <EnumCombobox id="screenwriter.hairColor" dictionary="colors" nullable nullLabel="Не указан"
                                  value={form.screenwriter.hairColor} onChange={(v) => setSw("hairColor", v)} />
                  ))}
                  {select("screenwriter.nationality", "Национальность", (
                    <EnumCombobox id="screenwriter.nationality" dictionary="countries" value={form.screenwriter.nationality}
                                  invalid={!!errors["screenwriter.nationality"]} onChange={(v) => setSw("nationality", v)} />
                  ))}
                </FieldGroup>
              )}
            </FieldSet>
          </FieldGroup>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>Отмена</Button>
            <Button type="submit" disabled={submitting}>{editing ? "Сохранить" : "Добавить"}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
