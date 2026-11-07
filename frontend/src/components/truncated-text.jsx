import {truncateText} from "@/lib/format"

export function TruncatedText({value, maxLength = 40}) {
    const text = truncateText(value, maxLength)
    const truncated = text !== value

    return (
        <span title={truncated ? value : undefined} aria-label={truncated ? value : undefined}>
      {text}
    </span>
    )
}
