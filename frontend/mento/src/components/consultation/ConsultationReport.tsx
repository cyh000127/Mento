interface ReportData {
  sessions?: {
    sessionId?: string
    sessionTitle?: string
    subSections?: {
      subTitle?: string
      descriptions?: string[]
    }[]
  }[]
}

interface ConsultationReportProps {
  report?: ReportData
}

export function ConsultationReport({ report }: ConsultationReportProps) {
  if (!report?.sessions || report.sessions.length === 0) {
    return null
  }

  return (
    <div className="space-y-12">
      {report.sessions.map((session, sessionIndex) => (
        <section key={session.sessionId ?? sessionIndex} className="space-y-6">
          {/* Session Title */}
          {session.sessionTitle && (
            <h2 className="text-lg font-bold pl-3 border-l-4 border-primary-500">
              {session.sessionTitle}
            </h2>
          )}

          {/* Sub Sections */}
          <div className="space-y-4">
            {session.subSections?.map((sub, subIndex) => (
              <div
                key={subIndex}
                className="border border-border rounded-md px-6 py-5 space-y-4"
              >
                {/* Sub Title */}
                {sub.subTitle && (
                  <h3 className="text-sm font-semibold text-foreground">
                    {sub.subTitle}
                  </h3>
                )}

                {/* Descriptions */}
                {sub.descriptions && sub.descriptions.length > 0 && (
                  <ul className="space-y-2">
                    {sub.descriptions.map((desc, descIndex) => (
                      <li
                        key={descIndex}
                        className="flex gap-3 text-sm text-muted-foreground leading-relaxed border-b border-dashed border-border pb-2 last:border-none"
                      >
                        <span className="text-muted-foreground font-medium min-w-[20px]">
                          {descIndex + 1}
                        </span>
                        <span>{desc}</span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        </section>
      ))}
    </div>
  )
}
